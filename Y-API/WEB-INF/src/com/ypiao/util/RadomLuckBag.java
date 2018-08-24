package com.ypiao.util;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @NAME:RadomLuckBag
 * @DESCRIPTION:
 * @AUTHOR:luxh
 * @DATE:2018/8/2
 * @VERSION:1.0
 */
public class RadomLuckBag {

    //每个红包的最小金额
    private static final double MIN = 0.01;

    private static Logger logger = Logger.getLogger(RadomLuckBag.class);

    class HongBao{
        //红包总金额
        private AtomicReference<BigDecimal> amount = new AtomicReference<BigDecimal>();
        //红包个数
        private AtomicInteger count;
        //红包总金额
        private BigDecimal hbAmount;
        private int hbCount;//红包个数
        //上面的两对参数分别对应CAS和锁操作
        public HongBao(BigDecimal amount,int count){
            this.amount.set(amount);
            this.count = new AtomicInteger(count);
            this.hbAmount = amount;
            this.hbCount = count;
        }

        /**
         * 用CAS操作随机分配一个红包
         * @return
         */
        public double assignHongBao(){
            while(true){
                //如果红包个数为0，则表示红包也被抢完，返回0
                if(count.get()<=0){
                    return 0.0;
                }
                //如果是最后一个红包则直接将剩余的金额返回
                if(count.get()==1){
                    BigDecimal c = amount.get();
                    if(amount.compareAndSet(c, new BigDecimal(0))){
                        count.decrementAndGet();
                        return c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }else{//如果最后一个红包金额修改失败，则表示最后一个红包已被抢走，已没有剩余红包，直接返回0
                        return 0.0;
                    }
                }
                BigDecimal balanceAmount = amount.get();
                int balanceCount = count.get();
                if(balanceCount==0){
                    return 0.0;
                }
                //求出剩余红包金额和个数的平均值
                double avg = balanceAmount.divide(new BigDecimal(balanceCount),8,BigDecimal.ROUND_HALF_UP).doubleValue();
                //随机获取一个MIN到2倍平均值之间的值
                double cur = getRandom(MIN, avg*2);
                //获取剩余金额和分配金额的差值
                double b = balanceAmount.add(new BigDecimal(-cur)).doubleValue();
                //由于每个红包至少是MIN这么大，此处获取剩余红包个数应该有的最小的红包金额，
                //比如MIN=0.01，那么如果这次分配之后还剩2个红包，则金额至少要剩下2分钱，不然不够分
                double c = new BigDecimal(balanceCount-1).multiply(new BigDecimal(MIN)).doubleValue();
                //分配之后剩余金额b  需大于等于   剩余的最小值c，如果不满足则重新分配红包大小，直到满足要求
                while(b < c){
                    cur = getRandom(MIN, avg*2);
                    b = balanceAmount.add(new BigDecimal(-cur)).doubleValue();
                }
                //如果是最后一个红包则直接将剩余的金额返回，
                //在返回结果之前再一次执行这个判断的目的是为了在多线程情况如果在返回结果之前已经被抢到只剩最后一个的时候
                //还是返回随机获得金额的话则会导致总金额不会被抢完
                if(count.get()==1){
                    BigDecimal c1 = amount.get();
                    if(amount.compareAndSet(c1, new BigDecimal(0))){
                        count.decrementAndGet();
                        return c1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }else{//如果最后一个红包金额修改失败，则表示最后一个红包已被抢走，已没有剩余红包，直接返回0
                        return 0.0;
                    }
                }
                //CAS更新金额和个数同时成功，则返回随机分配的红包金额
                if(amount.compareAndSet(balanceAmount, balanceAmount.add(new BigDecimal(-cur)))){
                    count.decrementAndGet();
                    return cur;
                }

            }


        }


        /**
         * 用锁控制
         * @return
         */
        public synchronized double assignHongBao2(){
            //如果红包个数为0，则表示红包也被抢完，返回0
            if(hbCount==0){
                return 0.0;
            }
            //如果是最后一个红包则直接将剩余的金额返回
            if(hbCount==1){
                hbCount--;
                return hbAmount.doubleValue();
            }
            //求出剩余红包金额和个数的平均值
            double avg = hbAmount.divide(new BigDecimal(hbCount),8,BigDecimal.ROUND_HALF_UP).doubleValue();
            //随机获取一个MIN到2倍平均值之间的值
            double cur = getRandom(MIN, avg*2);
            //获取剩余金额和分配金额的差值
            double b = hbAmount.add(new BigDecimal(-cur)).doubleValue();
            //由于每个红包至少是MIN这么大，此处获取剩余红包个数应该有的最小的红包金额，
            //比如MIN=0.01，那么如果这次分配之后还剩2个红包，则金额至少要剩下2分钱，不然不够分
            double c = new BigDecimal(hbCount-1).multiply(new BigDecimal(MIN)).doubleValue();
            //分配之后剩余金额b  需大于等于   剩余的最小值c，如果不满足则重新分配红包大小，直到满足要求
            while(b < c){
                cur = getRandom(MIN, avg*2);
                b = hbAmount.add(new BigDecimal(-cur)).doubleValue();
            }
            hbAmount = hbAmount.add(new BigDecimal(-cur));
            hbCount--;
            return cur;
        }

        /**
         * 计算两个数之间的随机值，结果保留两位小数
         * @param begin
         * @param end
         * @return
         */
        private double getRandom(double begin,double end){
            double random = Math.random();
            double amount =  random*(end - begin)+begin;
            BigDecimal bg = new BigDecimal(amount);
            return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

    }


    public static ConcurrentLinkedQueue<BigDecimal> getBag(BigDecimal hbAmount,int hbCount,BigDecimal lastEnvelopes) throws InterruptedException{
        BigDecimal lastEnvelopesNum =hbAmount.multiply(lastEnvelopes) ;
        hbAmount = hbAmount.subtract(lastEnvelopesNum).setScale(2, BigDecimal.ROUND_HALF_UP);
        RadomLuckBag h = new RadomLuckBag();
        final HongBao hb = h.new HongBao(hbAmount,hbCount);
        int THREAD_COUNT = hbCount;
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        final ConcurrentLinkedQueue<BigDecimal> total = new ConcurrentLinkedQueue<BigDecimal>();
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for(int i=0;i<THREAD_COUNT;i++){
            pool.execute(new Runnable() {
                public void run() {
                    double m = hb.assignHongBao();
                    total.add(new BigDecimal(m).setScale(2, BigDecimal.ROUND_HALF_UP));
					if(m>0){
                        logger.info(Thread.currentThread().getName()+"抢到:"+m);
					}else{
                        logger.info(Thread.currentThread().getName()+"没抢到红包");
					}
                    latch.countDown();
                }
            });
        }
        pool.shutdown();
        latch.await();
        BigDecimal amount = new BigDecimal(0.00);
        amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        Iterator<BigDecimal> it = total.iterator();
        while(it.hasNext()){
            amount= amount.add(it.next());
        }
//        amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //如果分配到的总金额和传入的总金额不相等
        if(amount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()!=hbAmount.doubleValue()){
            logger.info("amount:"+amount);
        }
        total.add(lastEnvelopesNum);
        return total;
    }

    public static void main(String[] args) throws InterruptedException{
        long start = System.currentTimeMillis();
            //10个红包，10个人抢
        BigDecimal last = new BigDecimal( "0.85").setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal amount = new BigDecimal( "20").setScale(2,BigDecimal.ROUND_HALF_UP);

        ConcurrentLinkedQueue<BigDecimal> asss = getBag(amount, 10,last);
        logger.info(asss.toString());
        BigDecimal sum = new BigDecimal("0.00");
        sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        for (BigDecimal aDouble : asss) {
            sum = sum.add(aDouble.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        logger.info(sum);
        logger.info("用时："+(System.currentTimeMillis()-start)+"ms");
    }
}
