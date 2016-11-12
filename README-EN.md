
Low power Bluetooth library. **advantage**:

1. adapter to Android5.0 and Android6.0 scan mode (faster).
2. suitable for xiaomi phone connection Bluetooth operation.
3. suitable for Samsung mobile phone discovery service, open notification, etc..
4. support hundreds of method in 'for()' function to write of data, and do not have to worry about it busy.
library use with a queue (like iOS bluetooth queue, you do not have to write delay method).
5. Support at the same time open multiple notification.
6. you can continuous use function such as write/read/notification in for(int i=0;i<100;i++) .

###NOTICE:

1. Android 6.0 phone need location permission when scan bluetooth.
2. write/read/notification operation must be after onServicesDiscovered() being called.
3. before you connect bluetoothLE device , suggest you close scan.

##GUIDE


