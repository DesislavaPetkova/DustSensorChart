int measurePin = A5;
int ledPower = 12;

unsigned int samplingTime = 280;
unsigned int deltaTime = 40;
unsigned int sleepTime = 9680;

float voMeasured = 0;
float calcVoltage = 0;
float dustDensity = 0;
int input=0;
void setup(){
  Serial.begin(9600); // opens serial port, sets data rate to 9600 bps
  pinMode(ledPower,OUTPUT);
}

void loop(){
  if(Serial.available()){
  input=Serial.read();
  if(input>-1 && input!=10){
    //Serial.println(input);
  digitalWrite(ledPower,LOW);
  delayMicroseconds(samplingTime);

  voMeasured = analogRead(measurePin);

  delayMicroseconds(deltaTime);
  digitalWrite(ledPower,HIGH);
  delayMicroseconds(sleepTime);

  calcVoltage = voMeasured*(5.0/1024);
  dustDensity = 0.17*calcVoltage-0.1;

  if ( dustDensity < 0)
  {
    dustDensity = 0.00;
  }

  Serial.print("volt: ");
  Serial.print(calcVoltage);

  Serial.print(" dens: ");
  Serial.println(dustDensity);

  delay(500);
  }
  }
}
