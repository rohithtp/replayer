package com.example.replayer;

import com.example.replayer.cli.ReplayCommand;
import picocli.CommandLine;

public class Main {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new ReplayCommand()).execute(args);
    System.exit(exitCode);
  }
}
