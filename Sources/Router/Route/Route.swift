//
//  Route.swift
//  Router
//
//  Created by Иван Галкин on 05.10.2025.
//

public protocol Route: Hashable, Identifiable {
    associatedtype T: Hashable

    var id: Int { get }
    var type: T { get }
    var arguments: (any RouteArgumentsProtocol)? { get }
}

extension Route {
    public static func == (lhs: Self, rhs: Self) -> Bool {
        lhs.id == rhs.id && lhs.type == rhs.type
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(type)
    }
}
