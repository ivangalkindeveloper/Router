//
//  RouterProtocol.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

import SwiftUI

public protocol RouterProtocol {
    associatedtype Screen: Hashable
    associatedtype Sheet: Hashable
    associatedtype FullScreenCover: Hashable
    
    var path: NavigationPath { get }
    var sheet: RouteSheet<Sheet>? { get }
    var fullScreenCover: RouteFullScreenCover<FullScreenCover>? { get }
    
    func push(
        route: RouteScreen<Screen>
    ) -> Void
    
    func root(
        route: RouteScreen<Screen>
    ) -> Void
    
    func push(
        route: RouteSheet<Sheet>
    ) -> Void
    
    func push(
        route: RouteFullScreenCover<FullScreenCover>
    ) -> Void
    
    func pop() -> Void
    
    func popRoot() -> Void
    
    func dismissSheet() -> Void
    
    func dismissFullScreenCover() -> Void
}
