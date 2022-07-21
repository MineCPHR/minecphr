package minecphr;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LibraryTest {
	@Test void emptyData() {
		assertThat(Transport.encode(new byte[0])).isEmpty();
	}

	@Test void samples() {
		checkSample(new byte[]{104, 101, 108, 108, 111}, "cphr0dpjkcmr0");
		checkSample(new byte[]{79, 80}, "cphr0fagq");
		checkSample(new byte[]{79, 49, 96}, "cphr0fuckq");
	}

	private void checkSample(byte[] data, String msg) {
		assertThat(Transport.encode(data)).containsExactly(msg);
		assertThat(Transport.decode(msg)).isEqualTo(data);
	}
}
