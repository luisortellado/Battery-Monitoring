//
//  BatteryModule.swift
//  BatteryMonitorApp
//
//  Created by Luis Carlos Ortellado Cabral on 2024-12-09.
//

import Foundation
import React

@objc(BatteryModule)
class BatteryModule: RCTEventEmitter {
  override static func requiresMainQueueSetup() -> Bool {
    return false
  }
  
  override func supportedEvents() -> [String]! {
    return ["onBatteryLevelChange"]
  }
  
  @objc func getBatteryLevel(_ resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) {
      UIDevice.current.isBatteryMonitoringEnabled = true
      DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
          let batteryLevel = UIDevice.current.batteryLevel
          if batteryLevel >= 0 {
              resolve(Int(batteryLevel * 100))
          } else {
              reject("ERROR", "Battery monitoring is not supported on this device or configuration.", nil)
          }
      }
  }
  
  @objc func startMonitoringBatteryLevel(_ resolve: @escaping RCTPromiseResolveBlock,
                                         reject: @escaping RCTPromiseRejectBlock) {
    do {
      UIDevice.current.isBatteryMonitoringEnabled = true
      NotificationCenter.default.addObserver(
        self,
        selector: #selector(batteryLevelDidChange), // Match this selector
        name: UIDevice.batteryLevelDidChangeNotification,
        object: nil
      )
      resolve("Battery monitoring started")
    } catch {
      reject("ERROR", "Failed to start battery monitoring", error)
    }
  }
  
  @objc private func batteryLevelDidChange() { // Renamed method
    let batteryLevel = UIDevice.current.batteryLevel
    if batteryLevel >= 0 {
      sendEvent(withName: "onBatteryLevelChange", body: ["level": Int(batteryLevel * 100)])
    }
  }
  
  deinit {
    NotificationCenter.default.removeObserver(self)
  }
}
