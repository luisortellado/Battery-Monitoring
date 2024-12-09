//
//  BatteryModule.m
//  BatteryMonitorApp
//
//  Created by Luis Carlos Ortellado Cabral on 2024-12-09.
//

#import <React/RCTBridgeModule.h>

// Esto asegura que el módulo sea accesible en React Native
@interface RCT_EXTERN_MODULE(BatteryModule, NSObject)

// Declara los métodos que exportas desde Swift
RCT_EXTERN_METHOD(getBatteryLevel:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(startMonitoringBatteryLevel:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

@end
