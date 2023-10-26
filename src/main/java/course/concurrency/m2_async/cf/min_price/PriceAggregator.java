package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        Queue<Double> prices = new ConcurrentLinkedQueue<>();
        try {
            CountDownLatch latch = new CountDownLatch(shopIds.size());
            shopIds.forEach(shop ->
                    new Thread(() -> {
                        prices.add(priceRetriever.getPrice(itemId, shop));
                        latch.countDown();
                    }).start());
            latch.await(2700, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        return prices.stream().min(Double::compareTo).orElse(Double.NaN);
    }
}
