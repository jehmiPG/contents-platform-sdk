using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.Networking;

public interface RequestResponsable<T> : Requestable<T>, Responsable<T>
{   
}
