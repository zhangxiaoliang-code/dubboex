package com.cast.vela.dubboex.api;

import java.util.concurrent.CompletableFuture;

public interface ServiceAsync {
	// 异步调用
//	CompletableFuture<Result> callAsync(PoJo poJo);
	CompletableFuture<String> callAsync(String name);
}
