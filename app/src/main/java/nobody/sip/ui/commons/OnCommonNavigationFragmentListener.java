package nobody.sip.ui.commons;

import java.util.List;

import nobody.sip.core.PlayerService.DetailsFragmentType;
import nobody.sip.core.PlayerService.ItemType;
import nobody.sip.core.SongSelector;
import nobody.sip.core.SongSelector.AddMode;
import nobody.sip.prots.IDProt;
import nobody.sip.prots.Song;

public interface OnCommonNavigationFragmentListener extends OnCommonFragmentListener {

	public void onAddingSongs(List<Song> songs, AddMode addMode);

	public void onAddingItem(ItemType itemType, Object item, SongSelector.AddMode addMode);
	
	public void onSendingToDetails(DetailsFragmentType detailsFragment, Object item);
}
