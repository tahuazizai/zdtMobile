package com.zdt.module.exceptions;

import com.zdt.module.enums.ErrorCodeEnum;
import com.zdt.module.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 * 
 * @Author scott
 * @Date 2019
 */
@RestControllerAdvice
@Slf4j
public class ZdtMobileExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(ZdtMobileException.class)
	public Result<?> handleRRException(ZdtMobileException e){
		log.error(e.getErrorMsg(), e);
		return Result.error(e.getErrorCode(), e.getErrorMsg());
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return Result.error(ErrorCodeEnum.SYSTEM_EXCEPTION.getCode(), ErrorCodeEnum.SYSTEM_EXCEPTION.getDesc());
	}


}
