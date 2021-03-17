package com.miaxis.gpioaidl;


interface IGPIOControl {

   int getGpio(int io);
   int setGpio(int io, boolean isTrue);
   int decodeIdCardPhoto(String wltData);

}