package com.ebay.magellan.tascreed.core.schedule.time;

import com.ebay.magellan.tascreed.depend.common.thread.DefaultThreadFactory;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TimeWheelTest {

    public class TestThreadFactory extends DefaultThreadFactory {
        protected AtomicInteger threadNumber = new AtomicInteger(2);
        TestThreadFactory() {
            super();
            namePrefix = "test-thread-";
        }
    }

    @Test
    public void testTimeWheel() throws Exception {
        TestThreadFactory factory = new TestThreadFactory();
        HashedWheelTimer hwt = new HashedWheelTimer(factory, 500, TimeUnit.MICROSECONDS);

        final AtomicLong st = new AtomicLong(System.currentTimeMillis());
        System.out.println(String.format("init at: %d", st.get()));

        TimerTask tt = new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                long t = System.currentTimeMillis();
                System.out.println(String.format("triggered at: %d, after %d, thread %s", t, t - st.get(), Thread.currentThread().getName()));
//                Thread.sleep(2000);
            }
        };

        st.set(System.currentTimeMillis());
        System.out.println(String.format("start at: %d", st.get()));
        System.out.println(Thread.currentThread().getName());

        for (int i = 0; i < 20; i++) {
//            Thread.sleep(1000);
//            long t = System.currentTimeMillis();
//            System.out.println(String.format("triggered at: %d, after %d, thread %s", t, t - st, Thread.currentThread().getName()));

            long t = st.get() + i * 200;
            long cur = System.currentTimeMillis();
            hwt.newTimeout(tt, t - cur, TimeUnit.MILLISECONDS);
        }

        Thread.sleep(5000);

        hwt.stop();
    }

}
