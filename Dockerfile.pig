FROM apache/hadoop:3.3.6

USER root

COPY pig-0.17.0 /opt/pig-0.17.0

ENV PIG_HOME=/opt/pig-0.17.0
ENV PATH=/opt/pig-0.17.0/bin:$PATH

CMD ["tail", "-f", "/dev/null"]
