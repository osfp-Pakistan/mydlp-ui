package com.mydlp.ui.dao;

import java.util.List;

import com.mydlp.ui.domain.Endpoint;

public interface EndpointDAO {

	public String registerNewEndpoint() throws RandomExhaustedException;
	
	public String getEndpointSecret(String endpointId);
	
	public String getEndpointAlias(String endpointId);
	
	public String getEndpointId(String endpointAlias);
	
	public List<Endpoint> getFilteredEndpoints(String searchString);
	
	public class RandomExhaustedException extends Throwable {

		private static final long serialVersionUID = 8782953817602918953L;
		
		public RandomExhaustedException() {
			super();
		}

		public RandomExhaustedException(String message, Throwable cause) {
			super(message, cause);
		}

		public RandomExhaustedException(String message) {
			super(message);
		}

		public RandomExhaustedException(Throwable cause) {
			super(cause);
		}

	}
}
