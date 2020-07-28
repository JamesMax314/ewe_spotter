package com.example.ewe_spotter;

import android.app.Activity;
import android.graphics.Bitmap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;

public class SheepNet{
    protected Interpreter tflite;
    /** Model for categorising */
    private MappedByteBuffer tfliteModel;
    private List<String> labels;
    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;
    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** Image size along the x axis. */
    private final int imageSizeX;
    /** Image size along the y axis. */
    private final int imageSizeY;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Output probability TensorBuffer. */
    private final TensorBuffer outputProbabilityBuffer;

    /** Processer to apply post processing of the output probability. */
    private final TensorProcessor probabilityProcessor;

    public enum Device {
        CPU,
        NNAPI,
        GPU
    }

    protected SheepNet(Activity activity, Device device, int numThreads) throws IOException {
        tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
//        labels = FileUtil.loadLabels(activity, getLabelPath());

        switch (device) {
            case NNAPI:
                NnApiDelegate nnApiDelegate = new NnApiDelegate();
                tfliteOptions.addDelegate(nnApiDelegate);
                break;
            case GPU:
                gpuDelegate = new GpuDelegate();
                tfliteOptions.addDelegate(gpuDelegate);
                break;
            case CPU:
                break;
        }
        tfliteOptions.setNumThreads(numThreads);
        tflite = new Interpreter(tfliteModel, tfliteOptions);

        // Reads type and shape of input and output tensors, respectively.
        // The input tensor is in position 0 in tflite.getInputTensor.
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input tensor.
        inputImageBuffer = new TensorImage(imageDataType);

        // Creates the output tensor and its processor.
        outputProbabilityBuffer =
                TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        // Creates the post processor for the output probability.
        probabilityProcessor = new TensorProcessor.Builder().build();
    }

    public void close() {
        tflite.close();
        tflite = null;
    }

    /** Runs inference and returns the classification results. */
    public int recognizeImage(final Bitmap bitmap) {
        inputImageBuffer = loadImage(bitmap);
        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
        float[] outArr = outputProbabilityBuffer.getFloatArray();
        int maxI = 0;
        for (int i=0; i < outArr.length; i++){
            if (outArr[i] > outArr[maxI]){
                maxI = i;
            }
        }
        close();
        return maxI;
    }

    /** Scales the image to match the input for the network */
    private TensorImage loadImage(Bitmap bitmap){
        inputImageBuffer.load(bitmap);
        // Ensure input is square
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());

        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    protected String getModelPath() {
        return "m5.tflite";
    }

//    protected String getLabelPath() {
//        return "m4_labels.txt";
//    }


}
