package cryptobot.tradingbot;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

/**
 * Hello world!
 *
 */
public class App {
	private static final String API_KEY = "vUkYiXXV1GnwPi1AFMXOSUO5ucIseEO8g67dzY6NYlaJmYUtBwUcdfj9dBFYgpGF";
	private static final String API_SECRET = "5HRO8QTmMl0MvYXfvPs7VF7SceGNLAOhORgquQlSOyI2kN7TxzLOhH8RnVTK8OP6";
	private static final BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, API_SECRET);
	private static final BinanceApiRestClient client = factory.newRestClient();
	private static final String[] symbolList = { "ICXBTC", "ETHBTC", "LSKBTC", "TRXBTC", "VENBTC", "ELFBTC", "NEOBTC",
			"XRPBTC", "XLMBTC", "EOSBTC", "IOSTBTC", "ADABTC", "HSRBTC", "BNBBTC", "LTCBTC", "CNDBTC", "PPTBTC",
			"WTCBTC", "BCCBTC", "ADXBTC", "DGDBTC", "GASBTC", "KNCBTC", "IOTABTC", "XVGBTC", "REQBTC", "ETCBTC",
			"OMGBTC", "BRDBTC", "SUBBTC", "POEBTC", "ZRXBTC", "QTUMBTC", "MDABTC", "XMRBTC", "BTGBTC", "BQXBTC",
			"VIBBTC", "BTSBTC", "AIONBTC", "MTLBTC", "STRATBTC", "LENDBTC", "APPCBTC", "GTOBTC", "LUNBTC", "SALTBTC" };
	private static final String REQUEST_URL = "https://slack.com/api/chat.postMessage?token=xoxp-264540871856-264540872416-307075926480-cc0b12248bf42be567bb768443e81265&channel=general&text=";

	public static void main(String[] args) {
		System.out.println("Hello World!");

		client.ping();
		long serverTime = client.getServerTime();
		System.out.println(serverTime);

		// trade history

		List<AggTrade> aggTrades = client.getAggTrades("VENBTC");
		Collections.reverse(aggTrades);
		System.out.println(aggTrades);
		// float lastPrice = Float.parseFloat(aggTrades.get(0).getPrice());

		// System.out.println("Ratio " + buySellRatio("VENBTC"));
		while (true) {
			try {
				sendSlackMessage(getBestSymbol());
				Thread.sleep(1000 * 60 * 3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// order book

		// Account account = client.getAccount();
		// System.out.println(account.getBalances());
		// System.out.println(account.getAssetBalance("VEN").getFree());
	}

	public static float buySellRatio(String pair) {
		OrderBook orderBook = client.getOrderBook(pair, 1000);
		// List<OrderBookEntry> asks = orderBook.getAsks();
		// OrderBookEntry firstAskEntry = asks.get(0);
		// System.out.println(firstAskEntry.getPrice() + " / " +
		// firstAskEntry.getQty());

		float bidsQty = getTotalQty(orderBook.getBids());
		float asksQty = getTotalQty(orderBook.getAsks());
		return bidsQty / asksQty;
	}

	public static String getBestSymbol() {
		HashMap<String, Float> symbolHashMap = new HashMap<String, Float>();
		String result = "";
		for (String symbol : symbolList) {
			try {
				symbolHashMap.put(symbol, buySellRatio(symbol));
			} catch (Exception e) {
				System.out.println("Error with symbol: " + symbol);
				e.printStackTrace();
			}
		}
		TreeMap<String, Float> sorted = new TreeMap<String, Float>();
		sorted.putAll(symbolHashMap);
		int i = 0;
		for (HashMap.Entry<String, Float> entry : entriesSortedByValues(sorted)) {
			i++;
			result += "Symbol_" + i + ": " + entry.getKey() + "\n";
			result += "Ratio_" + i + ": " + entry.getValue() + "\n\n\n";
			if (i > 3)
				break;
		}
		return result;
	}

	public static float getTotalQty(List<OrderBookEntry> entries) {
		float sum = 0;
		for (OrderBookEntry orderBookEntry : entries) {
			sum += Float.parseFloat(orderBookEntry.getQty());
		}
		return sum;
	}

	public static void sendSlackMessage(String message) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpget = new HttpGet(REQUEST_URL + URLEncoder.encode(message, "UTF-8"));

			System.out.println("Executing request " + httpget.getRequestLine());

			CloseableHttpResponse responseBody = httpclient.execute(httpget);
			System.out.println("----------------------------------------");
			System.out.println(responseBody);
		} finally {
			httpclient.close();
		}
	}

	static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				int res = e2.getValue().compareTo(e1.getValue());
				return res != 0 ? res : 1;
			}
		});
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
