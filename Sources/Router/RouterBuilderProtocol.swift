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
        associatedtype Content: View
    
        func build(route: RouteScreen<Screen>) -> Content
    
        func build(route: RouteSheet<Sheet>) -> Content
    
        func build(route: RouteFullScreenCover<FullScreenCover>) -> Content
}
