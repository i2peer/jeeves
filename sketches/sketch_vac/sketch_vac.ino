#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  MOTOR_INTERFACE1    8
#define  MOTOR_A             9
#define  MOTOR_B             10
#define  MOTOR_INTERFACE2    11
#define  MOTOR_INTERFACE3    12
#define  MOTOR_INTERFACE4    13
#define  SPEED               1000 


AndroidAccessory acc("Google, Inc.",
		     "DemoKit",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.android.com",
		     "0000000012345678");

void setup();
void loop();


void setup()
{
  Serial.begin(115200);
  Serial.print("\r\nStart");
  delay(500);
  acc.powerOn();
  pinMode(MOTOR_INTERFACE1,OUTPUT);
  pinMode(MOTOR_INTERFACE2,OUTPUT);
  pinMode(MOTOR_A,OUTPUT);
  
  pinMode(MOTOR_INTERFACE3,OUTPUT);
  pinMode(MOTOR_INTERFACE4,OUTPUT);
  pinMode(MOTOR_B,OUTPUT);
}

void changeMotorSpeed(int motor, int speed) {
     analogWrite(motor,speed);
}

void startMotor() {
 // if(motor == MOTOR_A) {
      digitalWrite(MOTOR_INTERFACE4,HIGH);
      digitalWrite(MOTOR_INTERFACE3,LOW);
 // } else if(motor == MOTOR_B) {
      digitalWrite(MOTOR_INTERFACE2,HIGH);
      digitalWrite(MOTOR_INTERFACE1,LOW);   
 // }   
}

void loop()
{
  byte msg[3];  
  if (acc.isConnected()) {
    Serial.print("\r\nAccount Connected: ");
    int len = acc.read(msg, sizeof(msg), 1);
    Serial.print(len);
    
    switch(msg[0]) {
      case 0x0:
        if(msg[1] == 0) {
          changeMotorSpeed(MOTOR_A, msg[2]);
        } else {
          changeMotorSpeed(MOTOR_B, msg[2]);
        }    
        break;
      case 0x1:
        break;
      default:
        break;
    }
    
    if(len > 0) {
      Serial.print("\r\nSetting Motor");
      
      startMotor();  
      
    } else {
      Serial.print("\r\nDid not receive message");
    }
  
  } else {
    Serial.print("\r\nAccount not Connected");
  }

   delay(3000);
}



void stop()
{
     digitalWrite(MOTOR_A,LOW);
     digitalWrite(MOTOR_B,LOW);
     delay(1000);
}

