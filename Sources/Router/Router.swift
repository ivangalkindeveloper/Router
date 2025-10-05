// The Swift Programming Language
// https://docs.swift.org/swift-book

import Foundation
import SwiftUI

final class Router<Screen: Hashable, Sheet: Hashable, FullScreenCover: Hashable>: RouterProtocol, ObservableObject {
    @Published var path: NavigationPath = NavigationPath()
    @Published var sheet: RouteSheet<Sheet>?
    @Published var fullScreenCover: RouteFullScreenCover<FullScreenCover>?
    
    func push(
        route: RouteScreen<Screen>
    ) -> Void {
        path.append(route)
    }
    
    func push(
        route: RouteSheet<Sheet>
    ) -> Void {
        self.sheet = route
    }
    
    func push(
        route: RouteFullScreenCover<FullScreenCover>
    ) -> Void {
        self.fullScreenCover = route
    }
    
    func pop() -> Void {
        path.removeLast()
    }
    
    func popRoot() -> Void {
        path.removeLast(path.count)
    }
    
    func dismissSheet() -> Void  {
        self.sheet = nil
    }
    
    func dismissFullScreenCover() -> Void {
        self.fullScreenCover = nil
    }
}
