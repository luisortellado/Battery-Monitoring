import React, {useEffect, useState} from 'react';
import {View, Text, Button, StyleSheet, Alert} from 'react-native';
import {NativeEventEmitter, NativeModules} from 'react-native';

const {BatteryModule} = NativeModules;

if (!BatteryModule) {
  console.error('BatteryModule is not available.');
}

const batteryEmitter = new NativeEventEmitter(BatteryModule);

const App: React.FC = () => {
  const [batteryLevel, setBatteryLevel] = useState<number | null>(null);

  useEffect(() => {
    console.log(BatteryModule, 'MODULES');
    if (!BatteryModule) {
      console.error('BatteryModule is not initialized properly.');
      return;
    }
    // Start monitoring battery level
    BatteryModule.startMonitoringBatteryLevel();

    // Subscribe to battery level changes
    const subscription = batteryEmitter.addListener(
      'onBatteryLevelChange',
      (data: {level: number}) => {
        setBatteryLevel(data.level);

        if (data.level < 0 || data.level > 100) {
          console.warn('Invalid battery level received:', data.level);
          return;
        }
        console.log(data.level, 'level');
        setBatteryLevel(data.level);
        if (data.level < 102) {
          Alert.alert('Low Battery', 'Battery level is below 20%!');
        }
      },
    );

    // Get the initial battery level
    BatteryModule.getBatteryLevel()
      .then((level: number) => setBatteryLevel(level))
      .catch((error: any) =>
        console.log('Error fetching battery level:', error),
      );

    return () => {
      subscription.remove();
      batteryEmitter.removeAllListeners('onBatteryLevelChange');
    };
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Battery Monitor</Text>
      <Text style={styles.text}>
        {batteryLevel !== null
          ? `Current Battery Level: ${batteryLevel}%`
          : 'Fetching battery level...'}
      </Text>
      <Button
        title="Get Battery Level"
        onPress={() => {
          if (!BatteryModule) {
            Alert.alert('Error', 'BatteryModule is not available.');
            return;
          }

          BatteryModule.getBatteryLevel()
            .then((level: number) => setBatteryLevel(level))
            .catch(() =>
              Alert.alert('Error', 'Failed to fetch battery level.'),
            );
        }}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    color: '#333',
  },
  text: {
    fontSize: 18,
    marginBottom: 20,
    color: '#555',
  },
});

export default App;
