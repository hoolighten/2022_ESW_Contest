// 참조
#include <SoftwareSerial.h>
#include <MsTimer2.h>
#include <TinyGPS.h>

// 상수
#define TXPIN 8 
#define RXPIN 9 
#define Waterlevel_1 2
#define Waterlevel_2 4
#define Waterlevel_3 6
#define Dir1Pin 3
#define Dir2Pin 5
#define LMButton 7

// 전역
// 클래스
TinyGPS gps;
SoftwareSerial uart_gps(TXPIN, RXPIN);

// 메소드(함수)
void getgps(TinyGPS &gps);

// 변수
// 플래그
bool state = 0; // 1b

long previousMillis; // 8b
float latitude, longitude; // 4b
uint8_t Waterlevel; // 1b

// 초기화
void setup() 
{
  // 핀 초기화
  pinMode(Dir1Pin, OUTPUT);
  pinMode(Dir2Pin, OUTPUT);
  pinMode(LMButton, INPUT);
  pinMode(Waterlevel_1, INPUT);
  pinMode(Waterlevel_2, INPUT);
  pinMode(Waterlevel_3, INPUT);
  
  // 시리얼 초기화
  Serial.begin(9600);
  uart_gps.begin(9600);

  // 타이머 초기화
  MsTimer2::set(100, linearmotor);
  MsTimer2::start();
}

// 루프
void loop() 
{
  long startMillis = millis();

  while((startMillis - previousMillis) > 10000) //시작시
  {
    // 갱신
    check_water_level();
    while(uart_gps.available())
    {
      int c = uart_gps.read();
      if(gps.encode(c))
      {
        getgps(gps);
      }
    }
    
    // 전송
    uint8_t data[9];
    uint8_t *lon = (uint8_t*) &longitude;
    uint8_t *lat = (uint8_t*) &latitude;

    data[0] = Waterlevel;
    data[1] = lat[0];
    data[2] = lat[1];
    data[3] = lat[2];
    data[4] = lat[3];
    data[5] = lon[0];
    data[6] = lon[1];
    data[7] = lon[2];
    data[8] = lon[3];

    Serial.write(data, 9);
    Serial.println("");
    // Serial.println(latitude);

    previousMillis = startMillis;
  }
}

void check_water_level()
{
  int sensorVal_1 = digitalRead(Waterlevel_1);
  int sensorVal_2 = digitalRead(Waterlevel_2);
  int sensorVal_3 = digitalRead(Waterlevel_3);

  if (sensorVal_1 and sensorVal_2 and sensorVal_3) //셋다 1이면 = 1
  {
    Waterlevel = 0;
  }
   else if (sensorVal_1 or sensorVal_2 and sensorVal_3 )
  {
    Waterlevel = 1;
  }
   else if (!(sensorVal_1 and sensorVal_2) and sensorVal_3)
  {
    Waterlevel = 2;
  }
   else if (!(sensorVal_1 and sensorVal_2 and sensorVal_3))
  {
    Waterlevel = 3;
  }
}

void getgps(TinyGPS &gps)
{
  gps.f_get_position(&latitude, &longitude);
  unsigned long chars;
  unsigned short sentences, failed_checksum;
  gps.stats(&chars, &sentences, &failed_checksum);
}

void linearmotor()
{
  int value = digitalRead(LMButton);
  if(value == 1)
  {
    if(state == 0)
    {
      state = 1;
    }
    else if(state == 1)
    {
      state = 0;
    }
  }
  if(state == 0)
  {
    digitalWrite(Dir1Pin, HIGH);
    digitalWrite(Dir2Pin, LOW);
  }
  else if(state == 1)
  {
    digitalWrite(Dir1Pin, LOW);
    digitalWrite(Dir2Pin, HIGH);
  }
}