/*
 * MibblePrinter.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2004 Per Cederberg. All rights reserved.
 */

package net.percederberg.mibble;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Iterator;

/**
 * A program that parses and prints a MIB file. If the MIB file(s)
 * specified on the command-line uses constructs or syntax that are
 * not supported, an error message will be printed to the standard
 * output.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  2.3
 * @since    2.0
 */
public class MibblePrinter {

    /**
     * The command-line help output.
     */
    private static final String COMMAND_HELP =
        "Prints the contents of an SNMP MIB file. This program comes with\n" +
        "ABSOLUTELY NO WARRANTY; for details see the LICENSE.txt file.\n" +
        "\n" +
        "Syntax: MibblePrinter <file or URL>";

    /**
     * The internal error message.
     */
    private static final String INTERNAL_ERROR =
        "INTERNAL ERROR: An internal error has been found. Please report\n" +
        "    this error to the maintainers (see the web site for\n" +
        "    instructions). Be sure to include the version number, as\n" +
        "    well as the text below:\n";

    /**
     * The application main entry point.
     *
     * @param args           the command-line parameters
     */
    public static void main(String[] args) {
        MibLoader  loader = new MibLoader();
        Mib        mib = null;
        File       file;
        URL        url;
        Iterator   iter;

        // Check command-line arguments
        if (args.length < 1) {
            printHelp("No MIB file or URL specified");
            System.exit(1);
        } else if (args.length > 1) {
            printHelp("Only one MIB file or URL may be specified");
            System.exit(1);
        }

        // Parse MIB file
        try {
            try {
                url = new URL(args[0]);
            } catch (MalformedURLException e) {
                url = null;
            }
            if (url == null) {
                file = new File(args[0]);
                loader.addDir(file.getParentFile());
                mib = loader.load(file);
            } else {
                mib = loader.load(url);
            }
            if (mib.getLog().warningCount() > 0) {
                mib.getLog().printTo(System.err);
            }
        } catch (FileNotFoundException e) {
            printError(args[0], e);
            System.exit(1);
        } catch (IOException e) {
            printError(args[0], e);
            System.exit(1);
        } catch (MibLoaderException e) {
            e.getLog().printTo(System.err);
            System.exit(1);
        } catch (RuntimeException e) {
            printInternalError(e);
            System.exit(1);
        }

        // Print MIB file
        iter = mib.getAllSymbols().iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
            System.out.println();
        }
    }

    /**
     * Prints command-line help information.
     *
     * @param error          an optional error message, or null
     */
    private static void printHelp(String error) {
        System.err.println(COMMAND_HELP);
        System.err.println();
        if (error != null) {
            System.err.print("Error: ");
            System.err.println(error);
            System.err.println();
        }
    }

    /**
     * Prints an internal error message. This type of error should
     * only be reported when run-time exceptions occur, such as null
     * pointer and the likes. All these error should be reported as
     * bugs to the program maintainers.
     *
     * @param e              the exception to be reported
     */
    private static void printInternalError(Exception e) {
        System.err.println(INTERNAL_ERROR);
        e.printStackTrace();
    }

    /**
     * Prints a file not found error message.
     *
     * @param file           the file name not found
     * @param e              the detailed exception
     */
    private static void printError(String file, FileNotFoundException e) {
        StringBuffer  buffer = new StringBuffer();

        buffer.append("Error: couldn't open file:");
        buffer.append("\n    ");
        buffer.append(file);
        System.out.println(buffer.toString());
    }

    /**
     * Prints a URL not found error message.
     *
     * @param url            the URL not found
     * @param e              the detailed exception
     */
    private static void printError(String url, IOException e) {
        StringBuffer  buffer = new StringBuffer();

        buffer.append("Error: couldn't open URL:");
        buffer.append("\n    ");
        buffer.append(url);
        System.out.println(buffer.toString());
    }
}
