package sorcer.sml.jobs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sorcer.test.ProjectContext;
import org.sorcer.test.SorcerTestRunner;
import sorcer.arithmetic.provider.impl.AdderImpl;
import sorcer.arithmetic.provider.impl.ArithmeticUtil;
import sorcer.arithmetic.provider.impl.MultiplierImpl;
import sorcer.arithmetic.provider.impl.SubtractorImpl;
import sorcer.core.SorcerConstants;
import sorcer.core.provider.rendezvous.ServiceJobber;
import sorcer.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static sorcer.co.operator.*;
import static sorcer.eo.operator.*;


/**
 * @author Mike Sobolewski
 */
@RunWith(SorcerTestRunner.class)
@ProjectContext("examples/sml")
public class LocalJobExertions implements SorcerConstants {

	private final static Logger logger = LoggerFactory.getLogger(LocalJobExertions.class);

	@Test
	public void jobPipeline() throws Exception {

		Task t3 = task(
				"t3",
				sig("subtract", SubtractorImpl.class),
				context("subtract", inEnt("arg/x1"), inEnt("arg/x2"),
						outEnt("result/y")));

		Task t4 = task(
				"t4",
				sig("multiply", MultiplierImpl.class),
				context("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 50.0),
						outEnt("result/y")));

		Task t5 = task(
				"t5",
				sig("add", AdderImpl.class),
				context("add", inEnt("arg/x1", 20.0), inEnt("arg/x2", 80.0),
						outEnt("result/y")));

		Job job = job(sig("exert", ServiceJobber.class),
				"j1", t4, t5, t3,
				pipe(outPoint(t4, "result/y"), inPoint(t3, "arg/x1")),
				pipe(outPoint(t5, "result/y"), inPoint(t3, "arg/x2")));

		Context context = upcontext(exert(job));
		logger.info("job context: " + context);
		assertTrue(value(context, "j1/t3/result/y").equals(400.0));

	}

	@Test
	public void nestedJob() throws Exception {

		Task t3 = task(
				"t3",
				sig("subtract", SubtractorImpl.class),
				context("subtract", inEnt("arg/x1"), inEnt("arg/x2"),
						outEnt("result/y", null)));

		Task t4 = task(
				"t4",
				sig("multiply", MultiplierImpl.class),
				context("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 50.0),
						outEnt("result/y")));

		Task t5 = task(
				"t5",
				sig("add", AdderImpl.class),
				context("add", inEnt("arg/x1", 20.0), inEnt("arg/x2", 80.0),
						outEnt("result/y")));

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		Job job = job(
				"j1", sig("exert", ServiceJobber.class),
				job("j2", t4, t5), t3,
				pipe(outPoint(t4, "result/y"), inPoint(t3, "arg/x1")),
				pipe(outPoint(t5, "result/y"), inPoint(t3, "arg/x2")));

		Context context = upcontext(exert(job));
		logger.info("job context: " + context);
		assertTrue(value(context, "j1/t3/result/y").equals(400.0));

	}

	@Test
	public void contexterService() throws Exception {

		// get a context for the template context in the task
		Task cxtt = task("addContext", sig("getContext", NetJobExertions.createContext()),
				context("add", input("arg/x1"), input("arg/x2")));

		Context result = context(exert(cxtt));
//		logger.info("contexter context: " + result);
		assertTrue(value(result, "arg/x1").equals(20.0));
		assertTrue(value(result, "arg/x2").equals(80.0));

	}
	
	@Test
	public void objectContexterTask() throws Exception {

		Task t5 = task("t5", sig("add", AdderImpl.class), 
					type(sig("getContext", NetJobExertions.createContext()), Signature.APD),
					context("add", inEnt("arg/x1"), inEnt("arg/x2"),
						result("result/y")));
		
		Context result = context(exert(t5));
//		logger.info("task context: " + result);
		assertTrue(value(result, "result/y").equals(100.0));

	}
	
	@Test
	public void exertJob() throws Exception {

		Task t3 = task("t3", sig("subtract", SubtractorImpl.class),
				cxt("subtract", inEnt("arg/x1"), inEnt("arg/x2"), outEnt("result/y")));

		Task t4 = task("t4",
				sig("multiply", MultiplierImpl.class),
				// cxt("multiply", in("super/arg/x1"), in("arg/x2", 50.0),
				cxt("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 50.0),
						outEnt("result/y")));

		Task t5 = task(
				"t5",
				sig("add", AdderImpl.class),
				cxt("add", inEnt("arg/x1", 20.0), inEnt("arg/x2", 80.0),
						outEnt("result/y")));

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		Job job = job(
				"j1",
				sig("exert", ServiceJobber.class),
				cxt(inEnt("arg/x1", 10.0),
						result("job/result", outPaths("j1/t3/result/y"))),
				job("j2", sig("exert", ServiceJobber.class), t4, t5), t3,
				pipe(outPoint(t4, "result/y"), inPoint(t3, "arg/x1")),
				pipe(outPoint(t5, "result/y"), inPoint(t3, "arg/x2")));

		Context context = upcontext(exert(job));
		logger.info("job context: " + context);
		assertTrue(value(context, "j1/t3/arg/x1").equals(500.0));
		assertTrue(value(context, "j1/t3/arg/x2").equals(100.0));
		assertTrue(value(context, "j1/t3/result/y").equals(400.0));

	}

	@Test
	public void evaluateJob() throws Exception {

		Task t3 = task("t3", sig("subtract", SubtractorImpl.class),
				cxt("subtract", inEnt("arg/x1"), inEnt("arg/x2"), outEnt("result/y")));

		Task t4 = task("t4",
				sig("multiply", MultiplierImpl.class),
				// cxt("multiply", in("super/arg/x1"), in("arg/x2", 50.0),
				cxt("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 50.0),
						outEnt("result/y")));

		Task t5 = task(
				"t5",
				sig("add", AdderImpl.class),
				cxt("add", inEnt("arg/x1", 20.0), inEnt("arg/x2", 80.0),
						outEnt("result/y")));

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		Job job = job(
				"j1",
				sig("exert", ServiceJobber.class),
				cxt(inEnt("arg/x1", 10.0),
						result("job/result", outPaths("j1/t3/result/y"))),
				job("j2", sig("exert", ServiceJobber.class), t4, t5), t3,
				pipe(outPoint(t4, "result/y"), inPoint(t3, "arg/x1")),
				pipe(outPoint(t5, "result/y"), inPoint(t3, "arg/x2")));

		Object result = evaluate(job);
		logger.info("job result: " + result);
		assertTrue(result.equals(400.0));

	}

	@Test
	public void arithmeticJobLocalExerter() throws Exception {

		Job exerter = ArithmeticUtil.createLocalJob();
		Context out = (Context) exerter.invoke(context(ent("j1/t3/result/y")));
//		logger.info("j1/t3/result/y: " + value);
		assertEquals(value(out, "j1/t3/result/y"), 400.0);

		// update inputs contexts
		Context multiplyContext = context("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 70.0));
		Context addContext = context("add", inEnt("arg/x1", 90.0), inEnt("arg/x2", 110.0));
		Context invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		out = (Context) exerter.invoke(invokeContext);
//		logger.info("j1/t3/result/y: " + value);
		assertEquals(value(out, "j1/t3/result/y"), 500.0);

		// update contexts partially
		multiplyContext = context("multiply", inEnt("arg/x1", 20.0));
		addContext = context("add", inEnt("arg/x1", 80.0));
		invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		out = (Context) exerter.invoke(invokeContext);
//		logger.info("j1/t3/result/y: " + value);
		assertEquals(value(out, "j1/t3/result/y"), 1210.0);

		// reverse the state to the initial one
		multiplyContext = context("multiply", inEnt("arg/x1", 10.0), inEnt("arg/x2", 50.0));
		addContext = context("add", inEnt("arg/x1", 80.0), inEnt("arg/x2", 20.0));
		invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		out = (Context) exerter.invoke(invokeContext);
//		logger.info("j1/t3/result/y: " + value);
		assertEquals(value(out, "j1/t3/result/y"), 400.0);
	}
}
