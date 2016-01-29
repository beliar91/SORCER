package edu.pjatk.inn.coffeemaker.impl;

import edu.pjatk.inn.coffeemaker.Delivery;
import edu.pjatk.inn.coffeemaker.ServiceService;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

/**
 * Created by Mike Sobolewski on 8/29/15.
 */
public class ServiceImpl implements ServiceService {

    @Override
    public Context serviceDo(Context context) throws RemoteException, ContextException {

        context.putValue("service/theone", 1);

        return context;
    }
}
