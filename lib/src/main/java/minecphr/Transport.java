package minecphr;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class Transport {
	public static final int CHUNK_LEN = 150;
	public static final String PREFIX = "cphr0";
	public static final String CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";

	private Transport() {}

	public static List<String> encode(byte[] payload) {
		var messages = new ArrayList<String>(payload.length/CHUNK_LEN);
		for (int i = 0; i < payload.length; i += 150) {
			byte[] chunk = convertBits(payload, i, i+150, 8, 5, true);
			char[] encodedChunk = new char[chunk.length];
			for (int j = 0; j < chunk.length; j++)
				encodedChunk[j] = CHARSET.charAt(chunk[j]);
			messages.add(PREFIX + new String(encodedChunk));
		}
		return messages;
	}

	public static byte[] decode(String msg) {
		msg = msg.toLowerCase();
		if (!msg.startsWith(PREFIX))
			return null;
		msg = msg.substring(PREFIX.length());

		byte[] data = new byte[msg.length()];
		for (int i = 0; i < data.length; i++) {
				var d = CHARSET.indexOf(msg.charAt(i));
				if (d < 0)
					// invalid character
					return null;
				data[i] = (byte)d;
		}
		return convertBits(data, 0, data.length, 5, 8, false);
	}

	/*
	 * ported from https://github.com/sipa/bech32/blob/master/ref/python/segwit_addr.py#L91
	 */
	private static byte[] convertBits(byte[] data, int offset, int length, int frombits, int tobits, boolean pad) {
		int acc = 0;
		int bits = 0;
		byte[] ret = new byte[((length-offset)*frombits+tobits-1)/tobits + (pad ? 1 : 0)];
		int j = 0;
		int maxv = (1 <<tobits)-1;
		int max_acc = (1<<(frombits+tobits-1))-1;
		for (int i = offset; i < length && i < data.length; i++) {
			if (data[i] < 0 || (data[i] >> frombits) != 0) {
				return null;
			}
			acc = ((acc << frombits) | data[i]) & max_acc;
			bits += frombits;
			while (bits >= tobits)  {
				bits -= tobits;
				ret[j++] = (byte)((acc >> bits) & maxv);
			}
		}
		if (pad && bits != 0)
				ret[j++] = (byte)((acc << (tobits - bits)) & maxv);
		else if (bits >= frombits || ((acc << (tobits - bits)) & maxv) != 0)
			return null;
		return Arrays.copyOf(ret, j);
	}
}
