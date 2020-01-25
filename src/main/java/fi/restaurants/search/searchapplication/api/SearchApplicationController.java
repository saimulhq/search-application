package fi.restaurants.search.searchapplication.api;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.trbl.blurhash.BlurHash;

@RestController
@RequestMapping("/restaurants")
public class SearchApplicationController {

	@Autowired
	ResourceLoader resourceLoader;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public List<JSONObject> search(@RequestParam String q, @RequestParam double lat, @RequestParam double lon) throws IOException, ParseException {
		// Parser for parsing JSON text
		JSONParser parser = new JSONParser();

		// Load "restaurants.json from the resources folder"
		Resource resource = resourceLoader.getResource("classpath:restaurants.json");
		// Parse the file as stream of bytes
		InputStream inputStream = resource.getInputStream();
		// Parse the inputstream as object
		Object obj = parser.parse(new InputStreamReader(inputStream, "UTF-8"));
		// Cast the obkect to JSONObject
		JSONObject jsonObject = (JSONObject) obj;

		// Fetching the list of restaurants
		JSONArray fullData = (JSONArray) jsonObject.get("restaurants");

		// Final response list after matching the search criteria
		List<JSONObject> searchResults = new ArrayList<JSONObject>();

		// Loop through the list of restaurants
		for(int i=0;i<fullData.size();i++) {
			// Fetch a JSONObject
			JSONObject restaurantData = (JSONObject) fullData.get(i); 

			// Fetch the atrributes from JSONObject
			String restaurantName = restaurantData.get("name").toString();
			String restarurantDescription = restaurantData.get("description").toString();
			List<String> tags = (List<String>) restaurantData.get("tags");
			List<Double> restaurantLocation = (List<Double>) restaurantData.get("location");
			double restaurantLat = restaurantLocation.get(1);
			double restaurantLon = restaurantLocation.get(0);
			String restaurantImage = restaurantData.get("image").toString();
			String imageBlurhash = restaurantData.get("blurhash").toString();
			String restaurantCity = restaurantData.get("city").toString();
			String currency = restaurantData.get("currency").toString();
			long deliveryPrice = (long) restaurantData.get("delivery_price");
			boolean isRestaurantOnline =  (boolean) restaurantData.get("online");

			// If the q string has a single parameter, try matching it with the first letter of name
			if(q.length()==1 && restaurantName.toLowerCase().startsWith(q)) {
				double distance = calculateHaversineDistance(lat, lon, restaurantLat, restaurantLon);

				if(distance<3) {
					// JSONObject for matched result
					JSONObject restaurantDetails = new JSONObject();

					restaurantDetails.put("blurhash", imageBlurhash);
					// To check the validity of the blurhash, uncomment the next line
					// restaurantDetails.put("validBlurhash", verifyBlurhash(restaurantImage, imageBlurhash));
					restaurantDetails.put("city", restaurantCity);
					restaurantDetails.put("currency", currency);
					restaurantDetails.put("delivery_price", deliveryPrice);
					restaurantDetails.put("description", restarurantDescription);
					restaurantDetails.put("image", restaurantImage);
					restaurantDetails.put("location", restaurantLocation);
					restaurantDetails.put("name", restaurantName);
					restaurantDetails.put("online", isRestaurantOnline);
					restaurantDetails.put("tags", tags);

					// Add the matched results to the list of JSONObjects
					searchResults.add(restaurantDetails);
				}
			}
			else if(q.length()>1 && (restaurantName.toLowerCase().contains(q) || restarurantDescription.toLowerCase().contains(q) || tags.contains(q))) {

				double distance = calculateHaversineDistance(lat, lon, restaurantLat, restaurantLon);
				if(distance<3) {
					// JSONObject for matched result
					JSONObject restaurantDetails = new JSONObject();

					restaurantDetails.put("blurhash", imageBlurhash);
					// To check the validity of the blurhash, uncomment the next line
					// restaurantDetails.put("validBlurhash", verifyBlurhash(restaurantImage, imageBlurhash));
					restaurantDetails.put("city", restaurantCity);
					restaurantDetails.put("currency", currency);
					restaurantDetails.put("delivery_price", deliveryPrice);
					restaurantDetails.put("description", restarurantDescription);
					restaurantDetails.put("image", restaurantImage);
					restaurantDetails.put("location", restaurantLocation);
					restaurantDetails.put("name", restaurantName);
					restaurantDetails.put("online", isRestaurantOnline);
					restaurantDetails.put("tags", tags);

					// Add the matched results to the list of JSONObjects
					searchResults.add(restaurantDetails);
				}
			}
		}

		return searchResults;
	}

	// Function for calculating the Haversine Distance
	private Double calculateHaversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
		final int R = 6371;
		Double latDistance = convertToRad(lat2-lat1);
		Double lonDistance = convertToRad(lon2-lon1);
		Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
				Math.cos(convertToRad(lat1)) * Math.cos(convertToRad(lat2)) * 
				Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double distance = R * c;

		return distance;
	}

	private Double convertToRad(Double value) {
		return value * Math.PI / 180;
	}

	// Implementation of the bonus task
	private boolean verifyBlurhash(String restaurantImage, String imageBlurhash) throws IOException {
		URL imageUrl = new URL(restaurantImage);
		BufferedImage image = ImageIO.read(imageUrl);
		String blurHash = BlurHash.encode(image);

		System.out.println("Provided blurhash: " + imageBlurhash);
		System.out.println("Calulated blurhash: " + blurHash + "\n");

		if(blurHash.equals(imageBlurhash)) {
			return true;
		}

		return false;
	}
}
