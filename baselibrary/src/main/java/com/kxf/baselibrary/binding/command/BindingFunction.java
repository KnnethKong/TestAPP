package com.kxf.baselibrary.binding.command;


/**
 * Represents a function with zero arguments.
 *
 * @param <T> the result type
 */
public interface BindingFunction<T> {
    T call();
}
