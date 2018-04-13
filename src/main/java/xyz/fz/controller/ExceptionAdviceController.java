package xyz.fz.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fz.model.Result;
import xyz.fz.util.BaseUtil;

/**
 * Created by fz on 2015-5-29.
 */
@ControllerAdvice
public class ExceptionAdviceController {

    private static final Logger logger = Logger.getLogger(ExceptionAdviceController.class);

    private static final String MISSING_BODY = "request body is missing";

    private static final String EXCEPTION = "Exception";

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result processException(Exception e) {

        logger.error(BaseUtil.getExceptionStackTrace(e));
        if (StringUtils.contains(e.getMessage(), MISSING_BODY)) {
            return Result.ofMessage("params is missing");
        } else if (StringUtils.contains(e.getMessage(), EXCEPTION)) {
            return Result.ofMessage("exception occurred");
        } else {
            return Result.ofMessage(e.getMessage());
        }
    }
}
