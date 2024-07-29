package com.jingxi.lib.paging.exception;

public class PagingException {

    public static class NoMoreDataException extends Exception{}

    public static class NoDataException extends NoMoreDataException{}
}
