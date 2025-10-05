//
//  RouterBuilderProtocol.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

import SwiftUI

public protocol RouterBuilderProtocol {
        associatedtype Screen: Hashable
        associatedtype Sheet: Hashable
        associatedtype FullScreenCover: Hashable
    
        @ViewBuilder
        func build(route: RouteScreen<Screen>) -> AnyView
    
        @ViewBuilder
        func build(route: RouteSheet<Sheet>) -> AnyView
    
        @ViewBuilder
        func build(route: RouteFullScreenCover<FullScreenCover>) -> AnyView
}
