package org.nanohttpd.protocols.http;

import java.util.Map;

public interface IMimeTypes {

	Map<String, String> mappingMimeTypes(Map<String, String> MIME_TYPES);
	
}
