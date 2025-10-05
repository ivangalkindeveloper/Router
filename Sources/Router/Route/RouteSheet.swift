//
//  RouteSheet.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

struct RouteSheet<T: Hashable>: Route {
    init(
        _ type: T,
        _ arguments: (any RouteArgumentsProtocol)? = nil
    ) {
        self.id = type.hashValue
        self.type = type
        self.arguments = arguments
    }

    let id: Int
    let type: T
    let arguments: (any RouteArgumentsProtocol)?
}
