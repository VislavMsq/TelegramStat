package org.stb.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice
public class ResponseExceptionHandler {

//    @ExceptionHandler()
//    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(Exception ex) {
//
//        ErrorResponse errorResponse = ErrorResponse.builder(
//                        ex, HttpStatus.BAD_REQUEST,
//                        ex.getMessage())
//                .build();
//        return ResponseEntity.ofNullable(errorResponse);
//    }
//
//    //    @ExceptionHandler({InvalidIdException.class, DataNotExistException.class})
////    public ResponseEntity<ErrorResponse> notFoundExceptionHandler(Exception ex) {
////        ErrorResponse errorResponse = ErrorResponse.builder(
////                        ex, HttpStatus.NOT_FOUND,
////                        ex.getMessage())
////                .build();
////        return ResponseEntity.ofNullable(errorResponse);
////    }
//    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    public String forbidden() {
//        return "redirect:/home";
//    }
}
