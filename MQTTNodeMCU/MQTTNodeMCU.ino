
/*
  Name:		NodeMCU.ino
  Created:	28.06.2019 17:00:15
  Author:	kiefe
*/



#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

#include <IRremoteESP8266.h>
#include <IRrecv.h>
#include <IRutils.h>
#include <IRsend.h>
#include <stdlib.h>


String ssid;
String pass;
String ip;

int recievePin = D3;

IRsend irsend(4);

IRrecv irrecv(recievePin);
decode_results results;

WiFiClient wifiClient;
PubSubClient client(wifiClient);
long lastMsg = 0;
char msg[50];
int value = 0;

void setup_wifi() {
  delay(10);

  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, pass);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi coonnected");
  Serial.print("IP address: ");
  String locIP = WiFi.localIP().toString();
  Serial.println(locIP);
}

void callback(char* topic, byte* payload, unsigned int length) {

  String message = "";
  for (int i = 0; i < length; i++) {
    message = message + (char)payload[i];
  }


  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("]: ");
  Serial.println(message);


  //if (topic == "TVSend") {
  long sendCode = atol(message.c_str());
  Serial.println(sendCode);
  irsend.sendRC5(sendCode);
  Serial.println("sending Signal");
  //}

  Serial.println(message);
}

void reconnect() {
  while (!client.connected() && (WiFi.status() == WL_CONNECTED)) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      client.publish("outTopic", "hello world");
      // ... and resubscribe
      client.subscribe("inTopic");
      client.subscribe("TVSend");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

// the setup function runs once when you press reset or power the board
void setup() {
  pinMode(BUILTIN_LED, OUTPUT);
  pinMode(4, OUTPUT);
  Serial.begin(115200);
  EEPROM.begin(512);

  if (getServer()) {
    Serial.println("Starting Wifi-Setup");
    setup_wifi();
    Serial.print("Setting MQTT server to ");
    Serial.println(ip);
    client.setServer(ip.c_str(), 1883);
    client.setCallback(callback);
    irrecv.enableIRIn();
  } else {
    delay(5000);
    Serial.println("no valid data stored");

    storeServer();

  }
}

// the loop function runs over and over again until power down or reset
void loop() {

  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  if (irrecv.decode(&results)) {
    String type = typeToString(results.decode_type);
    String val = uint64_tToHex(results.value);
    String message = String("Recieved on " + type);
    message += ": ";
    message += val;
    Serial.println(message);
    client.publish("irOut", message.c_str());
    delay(5);
    irrecv.resume();
  }

}

String uint64_tToHex(uint64_t uint) {
  char buffer[100];
  sprintf(buffer, "0x%x", uint);
  String hexString = buffer;
  return (hexString);
}

uint64_t hexToUint64_t(String hexStr) {
  const char* buffer = hexStr.c_str();
  int uint = 0;
  uint = (int) strtol( &hexStr[1], NULL, 16);
  return uint;
  Serial.print(uint);
}


void writeString(char add, String data)
{
  int _size = data.length();
  int i;
  for (i = 0; i < _size; i++)
  {
    EEPROM.write(add + i, data[i]);
  }
  EEPROM.write(add + _size, '\0'); //Add termination null character for String Data
  EEPROM.commit();
}


String read_String(char add)
{
  int i;
  char data[100]; //Max 100 Bytes
  int len = 0;
  unsigned char k;
  k = EEPROM.read(add);
  if (k == 0xFF) {
    return "";
  }
  while (k != '\0' && len < 500) //Read until null character
  {
    k = EEPROM.read(add + len);
    data[len] = k;
    len++;
  }
  data[len] = '\0';
  return String(data);
}

boolean getServer() {
  String ssidOut = read_String(100);
  ssidOut = ssidOut.substring(0, ssidOut.length() - 1);

  String passOut = read_String(200);
  passOut = passOut.substring(0, passOut.length() - 1);

  String ipOut = read_String(300);
  ipOut = ipOut.substring(0, ipOut.length() - 1);

  if (ssidOut == "" || passOut == "" || ipOut == "") {
    return false;
  } else {
    ssid = ssidOut;
    pass = passOut;
    ip = ipOut;
    return true;
  }
}

void storeServer() {
  Serial.println("Please enter ssid: "); //Prompt User for input
  while (Serial.available() == 0) {           //Wait for user input

  }
  String s_ssid = Serial.readString();
  Serial.println(s_ssid);
  writeString(100, s_ssid);

  Serial.println("Please enter password: "); //Prompt User for input
  while (Serial.available() == 0) {           //Wait for user input

  }
  String s_pass = Serial.readString();
  Serial.println(s_pass);
  writeString(200, s_pass);

  Serial.println("Please enter ipAddress: "); //Prompt User for input
  while (Serial.available() == 0) {           //Wait for user input

  }
  String s_ip = Serial.readString();
  Serial.println(s_ip);
  writeString(300, s_ip);

  setup();
}
