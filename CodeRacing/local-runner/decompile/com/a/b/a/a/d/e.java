package com.a.b.a.a.d;

import com.a.b.a.a.a.b;
import com.a.b.a.a.e.a.c.a;
import com.codeforces.commons.concurrent.AtomicUtil;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

class e
  extends KeyAdapter
{
  e(a parama) {}
  
  public void keyPressed(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.isConsumed()) {
      return;
    }
    int i = 1;
    if (paramKeyEvent.getKeyChar() == ' ')
    {
      AtomicUtil.invert(a.e(this.a));
    }
    else if (paramKeyEvent.getKeyCode() == 9)
    {
      AtomicUtil.invert(a.f(this.a));
    }
    else if (paramKeyEvent.getKeyCode() == 39)
    {
      if (a.g(this.a).incrementAndGet() > 32767) {
        a.g(this.a).decrementAndGet();
      }
    }
    else if (paramKeyEvent.getKeyCode() == 72)
    {
      AtomicUtil.decrement(a.h(this.a), 2);
    }
    else
    {
      long l;
      int j;
      if (paramKeyEvent.getKeyCode() == 38)
      {
        l = a.a(this.a).get();
        j = Arrays.binarySearch(a.c(), l);
        if (j > 0) {
          a.a(this.a).compareAndSet(l, a.c()[(j - 1)]);
        }
      }
      else if (paramKeyEvent.getKeyCode() == 40)
      {
        l = a.a(this.a).get();
        j = Arrays.binarySearch(a.c(), l);
        if (j < a.c().length - 1) {
          a.a(this.a).compareAndSet(l, a.c()[(j + 1)]);
        }
      }
      else if (paramKeyEvent.getKeyCode() == 18)
      {
        a.i(this.a).set(true);
      }
      else if (((paramKeyEvent.getKeyCode() == 521) || (paramKeyEvent.getKeyCode() == 61)) && (paramKeyEvent.isControlDown()))
      {
        a.j(this.a).lock();
        try
        {
          double d1 = a.k(this.a) / 2.0D;
          double d2 = a.l(this.a) / 2.0D;
          if ((a.k(this.a) > 1280.0D) && (NumberUtil.equals(Double.valueOf(d1), Double.valueOf(Math.round(d1)))) && (NumberUtil.equals(Double.valueOf(d2), Double.valueOf(Math.round(d2)))))
          {
            a.a(this.a, d1);
            a.b(this.a, d2);
          }
        }
        finally
        {
          a.j(this.a).unlock();
        }
      }
      else if ((paramKeyEvent.getKeyCode() == 45) && (paramKeyEvent.isControlDown()))
      {
        a.j(this.a).lock();
        try
        {
          if (a.k(this.a) < 20480.0D)
          {
            a.a(this.a, a.k(this.a) * 2.0D);
            a.b(this.a, a.l(this.a) * 2.0D);
          }
        }
        finally
        {
          a.j(this.a).unlock();
        }
      }
      else if ((paramKeyEvent.getKeyCode() == 48) && (paramKeyEvent.isControlDown()))
      {
        a.j(this.a).lock();
        try
        {
          a.a(this.a, 5120.0D);
          a.b(this.a, a.k(this.a) * a.m(this.a).c() / a.m(this.a).b());
        }
        finally
        {
          a.j(this.a).unlock();
        }
      }
      else
      {
        Long localLong;
        if (paramKeyEvent.getKeyCode() == 49)
        {
          localLong = (Long)a.n(this.a).get(Integer.valueOf(0));
          if (localLong != null) {
            a.o(this.a).set(localLong.longValue());
          }
        }
        else if (paramKeyEvent.getKeyCode() == 50)
        {
          localLong = (Long)a.n(this.a).get(Integer.valueOf(1));
          if (localLong != null) {
            a.o(this.a).set(localLong.longValue());
          }
        }
        else if (paramKeyEvent.getKeyCode() == 51)
        {
          localLong = (Long)a.n(this.a).get(Integer.valueOf(2));
          if (localLong != null) {
            a.o(this.a).set(localLong.longValue());
          }
        }
        else if (paramKeyEvent.getKeyCode() == 52)
        {
          localLong = (Long)a.n(this.a).get(Integer.valueOf(3));
          if (localLong != null) {
            a.o(this.a).set(localLong.longValue());
          }
        }
        else if (paramKeyEvent.getKeyCode() == 87)
        {
          a.p(this.a).a(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 83)
        {
          a.p(this.a).b(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 65)
        {
          a.p(this.a).d(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 68)
        {
          a.p(this.a).e(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 81)
        {
          a.p(this.a).f(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 69)
        {
          a.p(this.a).h(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 67)
        {
          a.p(this.a).c(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 88)
        {
          a.p(this.a).g(0, true);
        }
        else if (paramKeyEvent.getKeyCode() == 73)
        {
          a.p(this.a).a(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 75)
        {
          a.p(this.a).b(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 74)
        {
          a.p(this.a).d(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 76)
        {
          a.p(this.a).e(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 85)
        {
          a.p(this.a).f(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 79)
        {
          a.p(this.a).h(1, true);
        }
        else if ((paramKeyEvent.getKeyCode() == 46) || (paramKeyEvent.getKeyCode() == 160))
        {
          a.p(this.a).c(1, true);
        }
        else if ((paramKeyEvent.getKeyCode() == 44) || (paramKeyEvent.getKeyCode() == 153))
        {
          a.p(this.a).g(1, true);
        }
        else if (paramKeyEvent.getKeyCode() == 104)
        {
          a.p(this.a).a(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 101)
        {
          a.p(this.a).b(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 100)
        {
          a.p(this.a).d(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 102)
        {
          a.p(this.a).e(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 103)
        {
          a.p(this.a).f(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 105)
        {
          a.p(this.a).h(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 99)
        {
          a.p(this.a).c(2, true);
        }
        else if (paramKeyEvent.getKeyCode() == 98)
        {
          a.p(this.a).g(2, true);
        }
        else
        {
          i = 0;
        }
      }
    }
    if (i != 0) {
      paramKeyEvent.consume();
    }
  }
  
  public void keyReleased(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.isConsumed()) {
      return;
    }
    int i = 1;
    if (paramKeyEvent.getKeyCode() == 18) {
      a.i(this.a).set(false);
    } else if (paramKeyEvent.getKeyCode() == 87) {
      a.p(this.a).a(0, false);
    } else if (paramKeyEvent.getKeyCode() == 83) {
      a.p(this.a).b(0, false);
    } else if (paramKeyEvent.getKeyCode() == 65) {
      a.p(this.a).d(0, false);
    } else if (paramKeyEvent.getKeyCode() == 68) {
      a.p(this.a).e(0, false);
    } else if (paramKeyEvent.getKeyCode() == 81) {
      a.p(this.a).f(0, false);
    } else if (paramKeyEvent.getKeyCode() == 69) {
      a.p(this.a).h(0, false);
    } else if (paramKeyEvent.getKeyCode() == 67) {
      a.p(this.a).c(0, false);
    } else if (paramKeyEvent.getKeyCode() == 88) {
      a.p(this.a).g(0, false);
    } else if (paramKeyEvent.getKeyCode() == 73) {
      a.p(this.a).a(1, false);
    } else if (paramKeyEvent.getKeyCode() == 75) {
      a.p(this.a).b(1, false);
    } else if (paramKeyEvent.getKeyCode() == 74) {
      a.p(this.a).d(1, false);
    } else if (paramKeyEvent.getKeyCode() == 76) {
      a.p(this.a).e(1, false);
    } else if (paramKeyEvent.getKeyCode() == 85) {
      a.p(this.a).f(1, false);
    } else if (paramKeyEvent.getKeyCode() == 79) {
      a.p(this.a).h(1, false);
    } else if ((paramKeyEvent.getKeyCode() == 46) || (paramKeyEvent.getKeyCode() == 160)) {
      a.p(this.a).c(1, false);
    } else if ((paramKeyEvent.getKeyCode() == 44) || (paramKeyEvent.getKeyCode() == 153)) {
      a.p(this.a).g(1, false);
    } else if (paramKeyEvent.getKeyCode() == 104) {
      a.p(this.a).a(2, false);
    } else if (paramKeyEvent.getKeyCode() == 101) {
      a.p(this.a).b(2, false);
    } else if (paramKeyEvent.getKeyCode() == 100) {
      a.p(this.a).d(2, false);
    } else if (paramKeyEvent.getKeyCode() == 102) {
      a.p(this.a).e(2, false);
    } else if (paramKeyEvent.getKeyCode() == 103) {
      a.p(this.a).f(2, false);
    } else if (paramKeyEvent.getKeyCode() == 105) {
      a.p(this.a).h(2, false);
    } else if (paramKeyEvent.getKeyCode() == 99) {
      a.p(this.a).c(2, false);
    } else if (paramKeyEvent.getKeyCode() == 98) {
      a.p(this.a).g(2, false);
    } else {
      i = 0;
    }
    if (i != 0) {
      paramKeyEvent.consume();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */