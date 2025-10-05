//
//  RouteSheet.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

public struct RouteSheet<T: Hashable>: Route {
    init(
        _ type: T,
        _ arguments: (any RouteArgumentsProtocol)? = nil
    ) {
        self.id = type.hashValue
        self.type = type
        self.arguments = arguments
    }

    public let id: Int
    public let type: T
    public let arguments: (any RouteArgumentsProtocol)?
}
