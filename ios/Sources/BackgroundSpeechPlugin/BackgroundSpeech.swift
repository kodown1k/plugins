import Foundation

@objc public class BackgroundSpeech: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
