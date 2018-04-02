using System;

public interface Converable<T> : Responsable<T>
{
    T Convert(byte[] datas);
}
