package synapticloop.nanohttpd.utils;

/*
 * Copyright (c) 2013-2020 synapticloop.
 * 
 * All rights reserved.
 *
 * This source code and any derived binaries are covered by the terms and
 * conditions of the Licence agreement ("the Licence").  You may not use this
 * source code or any derived binaries except in compliance with the Licence.
 * A copy of the Licence is available in the file named LICENCE shipped with
 * this source code or binaries.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * Licence for the specific language governing permissions and limitations
 * under the Licence.
 */

public class AsciiArt {
	public static final String LINE = "                    +-------------------------------------+\n";

	public static final String ROUTEMASTER =
			"                           ....OND8OOOOOO88DDN=...         \n" +
			"                         ..D8OZ$$$$$$$$$77777777$OD.       \n" +
			"                         .D888OOOOZZZZZ$$77IIIIII7$8.      \n" +
			"                         ~II??+++====~~~~~::::::::~+.      \n" +
			"                         ??++===~~:::,,,,..........:.      \n" +
			"                        .I?++==~~~:::,,,,..........:.      \n" +
			"                        .I+=======:::::::,,::::::::=.      \n" +
			"                      . .888888OOOZZZZZZZ$ZZZZZZZZZ8~      \n" +
			"                         8OZ88OOOOZZZZ$$$???+++??I=$I      \n" +
			"                        .NO$8OOOOZZZZ$$$$I??++??II=7$      \n" +
			"                        .NZ$88NNNND88OZ$7I=~~=+III=78      \n" +
			"                        .NZ$8ZM8$78OI?I+??=~==+III=7N      \n" +
			"                        .NOZ88MNNDD88OZIII===+?III+7N..    \n" +
			"                      ...N8Z8OOOOOZZZZ$$$I???+??II+7M.     \n" +
			"                     ..,.7??????$Z$?I????ZZZ?????????~,..  \n" +
			"                     .?8.7?+++====+~~~~~~:::::::~~~=+??=   \n" +
			"                     .?O.7I??+++===:::::,,,,,.,,,::=???+   \n" +
			"                    ..88.7??????===::::,,,,.....,:,=?8I..  \n" +
			"                    . . .7??I??+==+::::,,,,.....,:,=?...   \n" +
			"                      . .7??????===~~::,,,..,..,,:,=?..    \n" +
			"                        .MM????+===~:::,,,..,...,:,=?..    \n" +
			"                        .ND888M++==::::,,,......,:,=?..    \n" +
			"                       ..ND88OOOO$8::::,,,,.....,,:=?..    \n" +
			"                       ..ND88OOOZZZZ$+NDDO888DDDDDDDM.     \n" +
			"                       ..ND+M~OOOZZZZ$$$$+N88OOO~D?DM.     \n" +
			"                       ..ND?=OOOOZZZZZZ$$7II?IMN~OZ8M      \n" +
			"                       ..NDD8:OOOZZZ$$Z$7$7II??I,$MMM.     \n" +
			"                       ..MMMMMMMMMMMMMMMMMMMMMMMMMMMN.     \n" +
			"                         .MMMMMM.............MMMOMM,.      \n" +
			"                    .:+$8NMMMMMMMMMMMMMMMMMMMMMMMMMMN8$?~,.\n\n";

	public static final String ROUTE_NOT_IN_SERVICE =
			LINE +
			"                    |   This route no longer in service.  |\n" +
			LINE;

	public static final String ROUTEMASTER_STARTED =
			LINE +
			"                    |     Routemaster up and running.     |\n" +
			LINE;

	private AsciiArt() {}

}
