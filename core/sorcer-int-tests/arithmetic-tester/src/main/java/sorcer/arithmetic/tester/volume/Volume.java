package sorcer.arithmetic.tester.volume;

/**
 * @author Mike Sobolewski
 */

import static java.lang.System.out;
import static sorcer.co.operator.ent;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.put;
import static sorcer.eo.operator.revalue;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Logger;

import sorcer.arithmetic.tester.volume.impl.Cylinder;
import sorcer.arithmetic.tester.volume.impl.Sphere;
import sorcer.service.Context;
import sorcer.service.ContextException;

@SuppressWarnings("rawtypes")
public class Volume implements Sphere, Cylinder, Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(Volume.class
			.getName());
	private static double radius = 0.0, height = 0.0;
	 
	public Volume() {}
	
	public Volume(Context context) {}
	
	public Context getSphereSurface(Context context) throws ContextException, RemoteException {
		double radius = (Double) revalue(context, "sphere/radius");
		put(context,
			ent("sphere/surface", 4.0 * Math.PI * Math.pow(radius, 3)));
		return context;
	}

	public Context getSphereVolume(Context context) throws ContextException, RemoteException {
		double radius = (Double) revalue(context, "sphere/radius");
		put(context, ent("sphere/volume",
			(4.0 / 3.0) * Math.PI * Math.pow(radius, 3)));
		return context;
	}

	public Context getCylinderSurface(Context context) throws ContextException, RemoteException {
		double radius = (Double) revalue(context, "cylinder/radius");
		double height = (Double) revalue(context, "cylinder/height");
		put(context, ent("cylinder/surface", 
				(2 * Math.PI * Math.pow(radius, 2))
						+ (2 * Math.PI * radius * height)));
		return context;
	}

	public Context getCylinderVolume(Context context) throws ContextException, RemoteException {
		double radius = (Double) revalue(context, "cylinder/radius");
		double height = (Double) revalue(context, "cylinder/height");
		put(context, ent("cylinder/volume", 
				Math.PI * Math.pow(radius, 2) * height));
		return context;
	}

	@SuppressWarnings("unchecked")
	public static void main(String... args) throws ContextException, RemoteException {
		Volume v = new Volume();
		if (args.length == 2 && args[0].equals("cylinder")
				&& args[1].equals("input")) {

			Scanner scanner = new Scanner(System.in);
			try {
				while (scanner.hasNextLine()) {
					processLine(scanner.nextLine());
				}
			} finally {
				scanner.close();
			}
			Context context = context(ent("cylinder/radius", radius),
					ent("cylinder/height", height));
			out.println("cylinder volume: " + v.getCylinderVolume(context));
		} else {
			if (args[0].equals("cylinder")) {
				Context context = context(ent("cylinder/radius", 2.0),
						ent("cylinder/height", 3.0));
				out.println("cylinder volume: " + v.getCylinderVolume(context));
			} else if (args[0].equals("sphere")) {
				Context context = context(ent("sphere/radius", 2.0));
				out.println("sphere volume: " + v.getSphereVolume(context));
			}
		}
	}
	
	private static void processLine(String line) {
		// use a second Scanner to parse the content of each line
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("=");
		if (scanner.hasNext()) {
			String key = scanner.next().trim();
			String value = scanner.next().trim();
			logger.info("key: " + key + " value: " + value);
			if (key.equals("cylinder/radius"))
				radius = new Double(value);
			else if (key.equals("cylinder/height"))
				height = new Double(value);
		} else {
			logger.info("Empty or invalid line. Unable to process.");
		}
		scanner.close();
		// no need to call scanner.close(), since the source is a String
	}

}
	
