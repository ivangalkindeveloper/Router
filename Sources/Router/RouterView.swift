//
//  RouterView.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

import SwiftUI

@available(macOS, unavailable)
public struct RouterView<
    Screen: Hashable,
    Sheet: Hashable,
    FullScreenCover: Hashable,
    B: RouterBuilderProtocol>
: View where B.Screen == Screen, B.Sheet == Sheet, B.FullScreenCover == FullScreenCover {
    public init(
        builder: B,
        initialRouteScreen: RouteScreen<Screen>
    ) {
        self.builder = builder
        self.initialRouteScreen = initialRouteScreen
    }
    
    @StateObject public var router: Router<Screen, Sheet, FullScreenCover> = Router()
    public var builder: B
    public let initialRouteScreen: RouteScreen<Screen>
    
    public var body: some View {
        NavigationStack(
            path: $router.path
        ) {
            builder.build(
                route: initialRouteScreen
            ).navigationDestination(
                for: RouteScreen<Screen>.self
            ) { route in
                builder.build(route: route)
            }.sheet(
                item: $router.sheet
            ) { route in
                builder.build(route: route)
            }
            .fullScreenCover(
                item: $router.fullScreenCover
            ) { route in
                builder.build(route: route)
            }
        }.environmentObject(router)
    }
}
