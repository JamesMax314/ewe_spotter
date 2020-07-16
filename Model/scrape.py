from bs4 import BeautifulSoup
import requests
import re
import sys
import os
import http.cookiejar
import json
import urllib.request, urllib.error, urllib.parse


def get_soup(url, header):
    return BeautifulSoup(urllib.request.urlopen(
        urllib.request.Request(url, headers=header)),
        'html.parser')

def bing_image_search(query, dir, N):
    query = query.split()
    query = '+'.join(query)
    url = "https://www.google.com/search?tbm=isch&q=" + query
    # url = "http://www.bing.com/images/search?q=" + query + "&FORM=HDRSC2"

    #add the directory for your image here
    header = {'User-Agent': "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36"}
    soup = get_soup(url, header)
    image_result_raw = soup.findAll("a", {"class":"iusc"})
    num = 0
    if len(image_result_raw) < N:
        num = len(image_result_raw)
    else:
        num = N
    print(num)

    for i in range(num):
        m = json.loads(image_result_raw[i]["m"])
        murl = m["murl"]
        try:
            image_name = urllib.parse.urlsplit(murl).path.split("/")[-1]
            if image_name[-3:] == "jpg":
                urllib.request.urlretrieve(murl, dir + query + str(i) + ".jpg")
        except:
            i -= 1
        print(i/num)



if __name__ == "__main__":
    N = 1000

    query = ["Soay sheep", "North Ronaldsay sheep", "Hebridean sheep", "Castlemilk Moorit", "Balwen sheep", "Badger Face sheep",
             "Eppynt Hill and Beulah Speckled Face", "Lleyn sheep", "Llanwenog sheep", "Wensleydale sheep", "Lonk sheep",
             "Herdwick sheep", "Manx Loaghtan", "Border Leicester sheep", "Suffolk sheep", "Cotswold sheep",
             "Portland sheep", "Southdown sheep", "Devon and Cornwall Longwool", "Greyface Dartmoor", "Exmoor Horn",
             ] #sys.argv[1]
    for i in range(len(query)):
        dir = "./Data/" + query[i] + "/"
        try:
            os.mkdir(dir)
        except:
            pass
        results = bing_image_search(query[i], dir, N)
