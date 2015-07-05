package nobody.sip.prots;

import java.util.ArrayList;

import nobody.sip.core.PlayerService;

public class Playlist extends IDProt {

	@Override
	public long getId() {
		return PlayerService.INVALID_ID_OR_POSITION;
	}

}
