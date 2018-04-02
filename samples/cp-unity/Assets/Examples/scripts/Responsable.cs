using System;

public interface Responsable<T>
{
    void onSucceed(T result);
    void onFailure(Exception e);
}
