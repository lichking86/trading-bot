package cryptobot.tradingbot;

import java.util.Collections;
import java.util.List;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
				"vUkYiXXV1GnwPi1AFMXOSUO5ucIseEO8g67dzY6NYlaJmYUtBwUcdfj9dBFYgpGF",
				"5HRO8QTmMl0MvYXfvPs7VF7SceGNLAOhORgquQlSOyI2kN7TxzLOhH8RnVTK8OP6");
		BinanceApiRestClient client = factory.newRestClient();
		client.ping();
		long serverTime = client.getServerTime();
		System.out.println(serverTime);

		// trade history

		List<AggTrade> aggTrades = client.getAggTrades("OMGBTC");
		Collections.reverse(aggTrades);
		System.out.println(aggTrades);
		float lastPrice = Float.parseFloat(aggTrades.get(0).getPrice());

		// order book

		OrderBook orderBook = client.getOrderBook("OMGBTC", 500);
		// List<OrderBookEntry> asks = orderBook.getAsks();
		// OrderBookEntry firstAskEntry = asks.get(0);
		// System.out.println(firstAskEntry.getPrice() + " / " +
		// firstAskEntry.getQty());

		float bidsQty = getTotalQty(orderBook.getBids());
		float asksQty = getTotalQty(orderBook.getAsks());
		System.out.println(bidsQty/lastPrice + " /\\/ " + asksQty/lastPrice);

		Account account = client.getAccount();
		System.out.println(account.getBalances());
		System.out.println(account.getAssetBalance("BTC").getFree());
	}

	public static float getTotalQty(List<OrderBookEntry> entries) {
		float sum = 0;
		for (OrderBookEntry orderBookEntry : entries) {
			sum += Float.parseFloat(orderBookEntry.getQty()) * Float.parseFloat(orderBookEntry.getPrice());
		}
		return sum;
	}
}
