import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Alert } from 'react-native';
import QRCodeScanner from 'react-native-qrcode-scanner';
import { RNCamera } from 'react-native-camera';

const ScannerScreen = () => {
  const [scanning, setScanning] = useState(true);

  const onSuccess = (e) => {
    setScanning(false);
    Alert.alert(
      'Scan Successful',
      `Scanned: ${e.data}`,
      [
        {
          text: 'OK',
          onPress: () => setScanning(true),
        },
      ]
    );
  };

  return (
    <View style={styles.container}>
      <Text style={styles.header}>QR Code Scanner</Text>
      {scanning && (
        <QRCodeScanner
          onRead={onSuccess}
          flashMode={RNCamera.Constants.FlashMode.auto}
          topContent={
            <Text style={styles.centerText}>
              Scan QR codes on packages, locations, or equipment
            </Text>
          }
          bottomContent={
            <TouchableOpacity style={styles.buttonTouchable}>
              <Text style={styles.buttonText}>Toggle Flash</Text>
            </TouchableOpacity>
          }
        />
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    padding: 20,
    backgroundColor: '#fff',
  },
  centerText: {
    fontSize: 16,
    padding: 32,
    color: '#777',
    textAlign: 'center',
  },
  buttonText: {
    fontSize: 18,
    color: '#2196F3',
    fontWeight: 'bold',
  },
  buttonTouchable: {
    padding: 16,
  },
});

export default ScannerScreen;