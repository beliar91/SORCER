package edu.pjatk.inn.coffeemaker;

import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

/**
 * Created by yyy on 2016-01-29.
 */
public interface ServiceService {
    public Context serviceDo(Context context) throws RemoteException, ContextException;

}
