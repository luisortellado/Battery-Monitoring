declare module 'react-native-battery-module' {
  interface BatteryModuleType {
    getBatteryLevel(): Promise<number>;
    startMonitoringBatteryLevel(): void;
  }

  const BatteryModule: BatteryModuleType;
  export default BatteryModule;
}
