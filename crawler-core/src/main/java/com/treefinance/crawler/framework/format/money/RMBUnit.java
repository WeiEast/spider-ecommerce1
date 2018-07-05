package com.treefinance.crawler.framework.format.money;

public enum RMBUnit {
    YUAN {
        @Override
        public double getConversion() {
            return 1;
        }
    },
    JIAO {
        @Override
        public double getConversion() {
            return 0.1;
        }
    },
    FEN {
        @Override
        public double getConversion() {
            return 0.01;
        }
    };

    public abstract double getConversion();
}
