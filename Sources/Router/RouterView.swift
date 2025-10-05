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
    R: Router<Screen, Sheet, FullScreenCover>,
    B: RouterBuilderProtocol>
: View where B.Screen == Screen, B.Sheet == Sheet, B.FullScreenCover == FullScreenCover {
    @StateObject var router: R
    var builder: B
    let initialRouteScreen: RouteScreen<Screen>
    
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
