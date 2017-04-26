package timer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

public class SoundEngine
{
	public void playSound()
	{
		// for including resource into jar
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("nokia_tune.mid");
		//File mediaFile = new File("nokia_tune.mid");
		
		try 
		{
			Sequencer sequencer = MidiSystem.getSequencer();
			sequencer.setSequence(MidiSystem.getSequence(is));
			sequencer.open();
			sequencer.start();
			
			while(true)
			{
				if(sequencer.isRunning())
				{
					try
					{
						Thread.sleep(1000);
					} 
					catch(InterruptedException ignore)
					{
						break;
					}
				}
				else 
				{
					break;
				}				
			}
			sequencer.stop();
			sequencer.close();
		}
		catch(MidiUnavailableException mue)
		{
			System.out.println("Midi device unavailable!");
		}
		catch (IOException ioe)
		{
			System.out.println("IO Error!");
		} 
		catch (InvalidMidiDataException e)
		{
			System.out.println("Invalid Midi data!");
		}		
	}
}
