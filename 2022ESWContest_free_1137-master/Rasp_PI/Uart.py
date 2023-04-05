import struct
import os, sys
import pymysql
import time
import RPi.GPIO as GPIO
import schedule
from serial import Serial

def get_time():
    _date = time.strftime('%Y-%m-%d')
    _time = time.strftime('%H:%M:%S')
    return _date, _time


def print_sch():
    if ser.in_waiting > 0:
        data = ser.read_until()
        day, tim = get_time()
        level = int(data[0])
        lat = struct.unpack('f', data[1:5])[0]
        lon = struct.unpack('f', data[5:9])[0]
        cur.execute(sql, (lat, lon, day, tim, level))
        con.commit()
        print(f"Day {day} Time {tim} Lv {level} Lat {lat} Lon {lon}")
    else:
        print("None")


def update_sql():
    con.close()


ser = Serial('/dev/ttyACM1', 9600)
ser.flush()

schedule.every(5).seconds.do(print_sch)

con = pymysql.connect(host='192.168.0.83', user='temp', password='s950j970**', db='minky', charset='utf8')
cur = con.cursor()

day, tim = get_time()
sql = "INSERT INTO map (위도, 경도, 날짜,시간,수위값) VALUES (%s, %s, %s, %s, %s)"

while True:
    schedule.run_pending()
    time.sleep(1)
