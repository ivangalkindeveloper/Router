// The Swift Programming Language
// https://docs.swift.org/swift-book

import Foundation
import SwiftUI

public final class Router<Screen: Hashable, Sheet: Hashable, FullScreenCover: Hashable>: RouterProtocol, ObservableObject {
    @Published public var path: NavigationPath = NavigationPath()
    @Published public var sheet: RouteSheet<Sheet>?
    @Published public var fullScreenCover: RouteFullScreenCover<FullScreenCover>?
    @Published var rootRoute: RouteScreen<Screen>
    
    public init(
        initialRoute: RouteScreen<Screen>,
    ) {
        self.rootRoute = initialRoute
    }
    
    public func push(
        route: RouteScreen<Screen>
    ) -> Void {
        self.path.append(route)
    }
    
    public func root(
        route: RouteScreen<Screen>
    ) -> Void {
        self.rootRoute = route
    }
    
    public func push(
        route: RouteSheet<Sheet>
    ) -> Void {
        self.sheet = route
    }
    
    public func push(
        route: RouteFullScreenCover<FullScreenCover>
    ) -> Void {
        self.fullScreenCover = route
    }
    
    public func pop() -> Void {
        self.path.removeLast()
    }
    
    public func popRoot() -> Void {
        self.path.removeLast(path.count)
    }
    
    public func dismissSheet() -> Void  {
        self.sheet = nil
    }
    
    public func dismissFullScreenCover() -> Void {
        self.fullScreenCover = nil
    }
}
